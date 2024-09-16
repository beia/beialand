FROM python:3.7.2
WORKDIR /srv/establish
RUN apt-get update && apt-get install libuv1
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY garmin garmin
COPY entry.sh .
EXPOSE 8080
CMD [ "./entry.sh" ]
