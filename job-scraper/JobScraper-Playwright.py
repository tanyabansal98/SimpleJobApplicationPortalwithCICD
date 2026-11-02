import csv
import json
from datetime import datetime
from urllib.parse import urlencode

from playwright.sync_api import sync_playwright

KEYWORDS = "software engineer intern"
LOCATION = "Boston, Massachusetts"
TIME_FILTER = "r86400"   # r86400 = last 24h, r604800 = last week
SORT_BY = "DD"           # DD = most recent
MAX_CLICKS = 3           # how many times to click "See more jobs"

params = urlencode({"keywords": KEYWORDS, "location": LOCATION,
                    "f_TPR": TIME_FILTER, "sortBy": SORT_BY})
URL = f"https://www.linkedin.com/jobs/search/?{params}"


def scrape():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)  # False = you can watch it
        page = browser.new_page()
        page.goto(URL, wait_until="domcontentloaded")
        page.wait_for_selector("ul.jobs-search__results-list li", timeout=15000)

        for _ in range(MAX_CLICKS):
            page.mouse.wheel(0, 4000)            # scroll down
            page.wait_for_timeout(2000)          # wait 2s (slow on purpose)
            button = page.locator("button.infinite-scroller__show-more-button")
            if button.count() and button.first.is_visible():
                button.first.click()
                page.wait_for_timeout(2000)

        jobs = []
        for card in page.locator("ul.jobs-search__results-list li").all():
            link = card.locator("a.base-card__full-link")
            if not link.count():
                continue
            time_tag = card.locator("time")
            jobs.append({
                "title": card.locator(".base-search-card__title").first.inner_text().strip(),
                "company": card.locator(".base-search-card__subtitle").first.inner_text().strip(),
                "location": card.locator(".job-search-card__location").first.inner_text().strip(),
                "posted": time_tag.first.get_attribute("datetime") if time_tag.count() else None,
                "url": link.first.get_attribute("href").split("?")[0],
            })
        browser.close()
        return jobs


def save(jobs):
    stamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    #with open(f"linkedin_pw_{stamp}.json", "w", encoding="utf-8") as f:
        #json.dump(jobs, f, indent=2)
    with open(f"linkedin_pw_{stamp}.csv", "w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=jobs[0].keys())
        writer.writeheader()
        writer.writerows(jobs)


if __name__ == "__main__":
    jobs = scrape()
    if jobs:
        save(jobs)
    print(f"Saved {len(jobs)} jobs")