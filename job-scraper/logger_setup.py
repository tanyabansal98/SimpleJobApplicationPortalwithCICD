import logging
import os
from datetime import datetime

LOG_DIR = "logs"


def get_logger(script_name):
    os.makedirs(LOG_DIR, exist_ok=True)                  # creates logs/ if missing
    stamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_path = os.path.join(LOG_DIR, f"{script_name}_{stamp}.log")

    logger = logging.getLogger(script_name)
    logger.setLevel(logging.INFO)                         # ignore DEBUG, keep INFO and above

    if not logger.handlers:                               # avoid adding handlers twice
        fmt = logging.Formatter("%(asctime)s [%(levelname)s] %(name)s - %(message)s",
                                datefmt="%Y-%m-%d %H:%M:%S")
        file_handler = logging.FileHandler(log_path, encoding="utf-8")
        file_handler.setFormatter(fmt)
        console_handler = logging.StreamHandler()         # also print to the Terminal
        console_handler.setFormatter(fmt)
        logger.addHandler(file_handler)
        logger.addHandler(console_handler)

    logger.info("Logging to %s", log_path)
    return logger