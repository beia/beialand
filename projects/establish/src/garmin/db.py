import ujson as json
from garmin.config import DB_LOCATION

class Store:
	users = []

	def save(self):
		with open(DB_LOCATION, "w") as f:
			json.dump(self.users, f)

	def load(self):
		try:
			with open(DB_LOCATION, "r") as f:
				json.load(users, f)
		except:
			pass

	def get_users(self):
		return list(self.users)

	def get_tokens(self):
		return [u.token for u in self.users]

	def add_user(self, token, secret):
		self.users.append(
			{
				"token": token,
				"secret": secret
			}
		)
		self.save()

	def find(self, token):
		return [u for u in self.users if u["token"] == token][0]

store = Store()
