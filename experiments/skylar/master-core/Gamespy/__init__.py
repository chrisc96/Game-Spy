import asyncio
import aioredis

from Gamespy.BaseScraper import BaseScraper
from Gamespy.SteamScraper import SteamScraper

ROOT_DIR = "."
SCRAPERS = [SteamScraper]
args = None

redisPool = None

async def preconnect_redis():
    global redisPool
    redisPool = await aioredis.create_redis_pool(
        "redis://localhost",
        minsize = 5,
        maxsize = 10)