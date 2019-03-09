FROM python:3.7.2
WORKDIR /srv/sentiment-analysis
COPY requirements.txt ./
RUN apt-get update && apt-get install libuv1
RUN pip install --no-cache-dir -r requirements.txt

COPY service.py .
EXPOSE 8080
CMD [ "python", "./service.py" ]
