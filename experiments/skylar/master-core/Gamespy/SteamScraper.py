import asyncio

import Gamespy

class SteamScraper:

    WORKER_LIMIT = 1

    async def worker(self):
        for i in range(10):
            print(i)
            await asyncio.sleep(1)