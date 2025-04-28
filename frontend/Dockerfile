# Build with node.js
FROM node:23-slim AS builder

WORKDIR /app

# Only copy package files first for better caching
COPY package.json package-lock.json ./

# Install clean dependencies inside container
RUN npm ci

# Copy the rest of the source code
COPY . .

# Build the static files
RUN npm run build

# Create final image with nginx
FROM nginx:stable-alpine

# Remove default nginx assets
RUN rm -rf /usr/share/nginx/html/*

# Copy the built dist folder from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]