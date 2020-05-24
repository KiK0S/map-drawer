import logging
import json
import requests
from telegram import *
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters, CallbackQueryHandler
logging.basicConfig(format = u'[%(filename)s{LINE:%(lineno)d}# %(levelname)-8s [%(asctime)s]  %(message)s]', level=logging.INFO, filename = u'bot.log')


TOKEN = json.load(open('secret_data'))['token']
url = json.load(open('secret_data'))['url']
local = False

def start(update, context):
	context.bot.send_message(update.effective_chat.id, 'Готов работать!')


def location(update, context):
	message = None
	if update.edited_message:
		message = update.edited_message
	else:
		message = update.message
		r = requests.post(url + "/add", data={"x": 0, "y": 0})
		if r.status_code == 200:
			logging.info("send message success")
		else:
			logging.info(str(r.status_code))
	current_pos = {"x": message.location.latitude, "y": message.location.longitude}
	r = requests.post(url + "/add", data=current_pos)
	if r.status_code == 200:
		logging.info("send message success")
	else:
		logging.info(str(r.status_code))

def main():
	updater = None
	if local:
		REQUEST_KWARGS = {
			'proxy_url': 'http://209.141.46.133:8080',
		}
		updater = Updater(TOKEN, use_context=True, request_kwargs=REQUEST_KWARGS)
	else:
		updater = Updater(TOKEN, use_context=True)

	dp = updater.dispatcher 
	location_handler = MessageHandler(Filters.location, location)
	dp.add_handler(CommandHandler("start", start))
	dp.add_handler(location_handler)
	updater.start_polling()
	updater.idle()
	print('here')


if __name__ == '__main__':
	main()
