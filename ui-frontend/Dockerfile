# Multi-stage build for Node.js
FROM node:18-alpine AS build

# Set working directory
WORKDIR /app

# Copy only package files first
COPY package*.json ./

# Install dependencies (include devDependencies for build)
RUN npm install

# Copy the rest of the app, but exclude node_modules
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built files
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port
EXPOSE 3000

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
