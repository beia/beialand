FROM python:3.7.2-alpine
WORKDIR /srv/sentiment-analysis
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY . .
CMD [ "python", "./service.py" ]
