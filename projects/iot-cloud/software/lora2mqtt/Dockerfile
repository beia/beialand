FROM tiangolo/uwsgi-nginx:python3.6

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY src/ /app