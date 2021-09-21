FROM node:lts-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN yarn install
COPY . .
RUN yarn build

FROM nginx:alpine
COPY ./nginx.conf /etc/nginx/conf.d/nginx.conf
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=builder /app/dist .
EXPOSE 9998
ENTRYPOINT ["nginx", "-g", "daemon off;"]