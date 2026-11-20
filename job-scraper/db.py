import os
from contextlib import contextmanager

import psycopg2
from dotenv import load_dotenv

from logger_setup import get_logger

load_dotenv()
log = get_logger("db")

def get_connection():
    log.info("Inside db/get_connection method()")
    host = os.getenv("PGHOST", "localhost")
    port = os.getenv("PGPORT", "5432")
    dbname = os.getenv("PGDATABASE", "jobportal")
    user = os.getenv("PGUSER", "postgres")

    log.info("Connecting to PostgreSQL %s:%s/%s as %s", host, port, dbname, user)
    try:
        conn = psycopg2.connect(
            host=host,
            port=port,
            dbname=dbname,
            user=user,
            password = os.environ["PGPASSWORD"], # no PGPASSWORD set -> KeyError, script stops
            connect_timeout=int(os.getenv("PGCONNECT_TIMEOUT", "10")), #int here converts the number 10 to int10 because by default os.getenv returns strings
        )
    except psycopg2.OperationalError:
        log.exception("Failed to connect to PostgreSQL")
        raise
    log.info("Connected to PostgreSQL")
    return conn

@contextmanager #contextmanager is an ibject that works with WITH and defines what setup and cleanup are
def db_cursor():
    conn=get_connection() # tries a postgres connection first
    try:
        with conn: #starts a transaction: a group of SQL statements which are either all saved (commit) or all undone (rollback). it does not close the connection.
            with conn.cursor() as cur: #conn.cursor() creates an object names cur which runs the SQL
                yield cur #pauses here and hands the cur to the caller. Now the caller function starts with it's transactions and finishes. The code resumes from here again saying the transactions either completed or everything is rolledback
        log.info("Transaction Committed")
    except Exception:
        log.exception("Transaction rolled back")
        raise #if error happens in callers block, error is raised in line 41 yield and transaction is rolled back
    finally: #this block always runs - success or error. it closes the connection
        conn.close()
        log.info("Connection closed")

if __name__ == "__main__": #This block runs only when this file is run individually, if another files does from db import db_cursor(), this block is skipped
    with db_cursor() as cur:
        cur.execute("SELECT version()") #runs this query in Postgres to ask for Version
        log.info("Server version: %s", cur.fetchone()[0]) #object.fetchone()[0] means returns one row as a tuple and [0] means first value


