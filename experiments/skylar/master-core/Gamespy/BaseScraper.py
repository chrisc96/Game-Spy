import asyncio

import Gamespy


class BaseScraper:

    WORKER_LIMIT = 1

    async def init(self):
        raise Exception("BaseScraper.init was not overwritten")