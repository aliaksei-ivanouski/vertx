import json
import requests
import random
import time

config = {'host': 'localhost',
          'port': 8082}


class Sender:
    def __init__(self, config):
        self.config = config
        self.headers = {"Content-Type":"application/json"}

    @staticmethod
    def __serialize_if_needed(message):
        if isinstance(message, str):
            return message

        return json.dumps(message)

    def save_post(self, data):
        payload = self.__serialize_if_needed(data)
        return requests.post(url='http://localhost:8082/api/v1/posts', headers=self.headers, data=payload)

    def update_post(self, post_id, data):
        payload = self.__serialize_if_needed(data)
        return requests.post(url='http://localhost:8082/api/v1/posts' + post_id, headers=self.headers, data=payload)

    def get_posts(self):
        return requests.get(url='http://localhost:8082/api/v1/posts', headers=self.headers)

    def delete_post(self):
        return requests.delete(url='http://localhost:8082/api/v1/posts-first', headers=self.headers)

def main():
    sender = Sender(config)

    data = {
        "title": "That is some title",
        "content": "This is article content"
    }

    for i in range(100000):
#         time.sleep(1)
        response = sender.save_post(data)
        print(response.text)

        response = sender.delete_post()
        print(response.text)

        response = sender.get_posts()
        print(response.text)


if __name__ == "__main__":
    main()
