FROM node:15.14.0-alpine3.10
WORKDIR /app
COPY ["package.json", "package-lock.json*", "./"]
RUN npm install --production --silent
COPY . .
CMD [ "npm", "start" ]