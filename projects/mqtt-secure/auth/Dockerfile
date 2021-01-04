FROM madnight/docker-alpine-wkhtmltopdf

FROM tiangolo/uwsgi-nginx:python3.6-alpine3.8
ARG DEBIAN_FRONTEND=noninteractive
FROM tiangolo/uwsgi-nginx:python3.6-alpine3.8
ARG DEBIAN_FRONTEND=noninteractive
RUN apk add --update --no-cache \
    freetype-dev \
    build-base python-dev py-pip jpeg-dev zlib-dev \
    postgresql-dev gcc python3-dev musl-dev \
    libgcc libstdc++ libx11 glib libxrender libxext libintl \
    libcrypto1.0 libssl1.0 \
    ttf-dejavu ttf-droid ttf-freefont ttf-liberation ttf-ubuntu-font-family \
	&& rm -rf /var/cache/apk/*
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . /app
RUN python3 manage.py collectstatic --noinput
