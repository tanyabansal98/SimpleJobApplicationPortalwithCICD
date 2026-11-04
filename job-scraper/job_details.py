import csv #Module used for reading and writing CSV files
import glob #Module to find file names matching a specified pattern
import json #Module used for reading and writing JSON files
import random #Module for generating random numbers
import re #Module for working with regular expressions i.e. regex
from datetime import datetime #Module for manipulating dates and times
import hashlib 
from urllib.parse import urlsplit

from playwright.sync_api import sync_playwright, TimeoutError as PlaywrightTimeout #Module for automating web browsers using Playwright; imports sync_playwright for synchronous API (each line finishes before the next starts) and aliases/renames TimeoutError to PlaywrightTimeout to avoid confusion with built-in TimeoutError

MAX_JOBS = None   #Test with 5 first; set to None for all jobs in the CSV

TITLE_SELECTORS = ["h1.top-card-layout__title", "h1"]
COMPANY_SELECTORS = ["a.topcard__org-name-link", ".topcard__flavor a", ".topcard__flavor"]
LOCATION_SELECTORS = [".topcard__flavor.topcard__flavor--bullet", ".topcard__flavor--bullet"]
DESCRIPTION_SELECTORS = ["div.show-more-less-html__markup", "div.description__text"]


def latest_csv():
    files = sorted(glob.glob("linkedin_pw_*.csv"))  # timestamp in name sorts oldest -> newest, glob only looks in current folder from which the file is running
    if not files:
        raise FileNotFoundError("No linkedin_pw_*.csv file found in this folder.")
    return files[-1] #Fetch the last item in the list i.e. index [-1]

#returns a list of URLs from the CSV file specified by the path
def read_urls(path):
    with open(path, newline="", encoding="utf-8") as f:
        return [row["url"] for row in csv.DictReader(f)]

#returns the job ID extracted from the URL using regex; if no match is found, returns None
def extract_job_id(url):
    #match = re.search(r"(\d+)/?$", url)  # digits at the very end of the URL
    #return match.group(1) if match else None
    
    if not url:
        return None
    match = re.search(r"(\d{6,})/?$", url)
    if match:
        return match.group(1)
    path = urlsplit(url).path.rstrip("/")
    if path:
        return "h_" + hashlib.sha1(path.encode()).hexdigest()[:16]
    return None


def first_text(page, selectors):
    for selector in selectors:
        loc = page.locator(selector)
        if loc.count():
            text = loc.first.inner_text().strip()
            if text:
                return text
    return None


def is_blocked(url):
    return any(word in url for word in ("authwall", "/login", "checkpoint", "/uas/"))


def scrape_job(page, url):
    page.goto(url, wait_until="domcontentloaded", timeout=30000)
    if is_blocked(page.url):
        raise RuntimeError("LinkedIn login wall")
    page.wait_for_selector("h1", timeout=15000)

    button = page.locator("button.show-more-less-html__button")  # "Show more" on description
    if button.count() and button.first.is_visible():
        try:
            button.first.click(timeout=3000)
        except PlaywrightTimeout:
            pass

    return {
        "job_id": extract_job_id(url),
        "position": first_text(page, TITLE_SELECTORS),
        "company": first_text(page, COMPANY_SELECTORS),
        "location": first_text(page, LOCATION_SELECTORS),
        "description": first_text(page, DESCRIPTION_SELECTORS),
        "url": url,
    }


if __name__ == "__main__":
    csv_path = latest_csv()
    urls = read_urls(csv_path)
    if MAX_JOBS:
        urls = urls[:MAX_JOBS]
    print(f"Reading {len(urls)} jobs from {csv_path}")

    results, failed = [], []
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        page = browser.new_context(locale="en-US").new_page()

        for i, url in enumerate(urls, start=1):
            try:
                job = scrape_job(page, url)
                results.append(job)
                print(f"[{i}/{len(urls)}] OK: {job['position']} @ {job['company']}")
            except RuntimeError as e:             # login wall: stop, retrying makes blocks last longer
                print(f"[{i}/{len(urls)}] BLOCKED: {e}. Stopping.")
                failed.append(url)
                break
            except Exception as e:                # any other failure: log it and continue
                print(f"[{i}/{len(urls)}] FAILED: {url} ({e})")
                failed.append(url)
            page.wait_for_timeout(random.randint(3000, 6000))  # 3-6 s pause between jobs

        browser.close()

    stamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    out = f"job_details_{stamp}.json"
    with open(out, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2, ensure_ascii=False)
    print(f"Saved {len(results)} jobs to {out}; {len(failed)} failed")