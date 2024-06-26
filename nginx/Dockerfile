FROM nginx:latest
COPY default.conf /etc/nginx/conf.d/default.conf
RUN mkdir -p /home/images
CMD ["nginx", "-g", "daemon off;"]