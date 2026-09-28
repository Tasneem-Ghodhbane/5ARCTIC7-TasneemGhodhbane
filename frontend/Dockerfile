# Stage 1: build the Angular app with Node
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: serve the built files with Nginx
FROM nginx:alpine
COPY --from=build /app/dist/*/browser /usr/share/nginx/html
EXPOSE 80
