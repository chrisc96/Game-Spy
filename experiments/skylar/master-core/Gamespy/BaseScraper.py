import asyncio
import aioredis

import Gamespy


class BaseScraper:

    WORKER_LIMIT = 1
    exclusiveRedis = None

    async def init(self):
        raise Exception("BaseScraper.init was not overwritten")



    '''
    '' Creates a Redis connection exclusive to this instance
    '' used for waiting on Redis task list '''
    async def create_exclusive_redis(self):
        self.exclusiveRedis = await aioredis.create_redis(
            "redis://localhost")