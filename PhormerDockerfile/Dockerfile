
# This Dockerfile is used to run Phormer docker container

FROM php:7.4-apache
COPY ../PhormerVersions/3.0.1 /var/www/html/
RUN apt-get update && apt-get install -y \
		libfreetype-dev \
		libjpeg62-turbo-dev \
		libpng-dev \
	&& docker-php-ext-configure gd --with-freetype --with-jpeg \
	&& docker-php-ext-install -j$(nproc) gd