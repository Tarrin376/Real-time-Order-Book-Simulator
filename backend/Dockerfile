FROM node:23.11-slim

WORKDIR /app

COPY package.json .

COPY app.js .

RUN npm install

CMD ["node", "app.js"]