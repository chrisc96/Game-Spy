import asyncio

import Gamespy


class BaseScraper:

    WORKER_LIMIT = 1

    async def worker(self):
        raise Exception("BaseScraper.worker was not overwritten")