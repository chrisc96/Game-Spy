import asyncio, aioredis

from Gamespy.BaseScraper import BaseScraper
from Gamespy.SteamScraper import SteamScraper

ROOT_DIR = "."
SCRAPERS = [SteamScraper]
args = None

redisPool = None

async def preconnect_redis():
    global redisPool
    redis = await aioredis.create_pool("redis://localhost")