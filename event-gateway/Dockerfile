# Build with node.js
FROM node:23.11-slim

WORKDIR /app

# Copy the package.json file to install dependencies
COPY ./package.json .

# Copy the source code
COPY ./src ./src

# Install all dependencies
RUN npm install

# Command to run the event gateway
CMD ["npm", "start"]