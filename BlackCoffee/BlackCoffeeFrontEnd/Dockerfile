FROM nginx:1.27-alpine

# Pulisci i file di default
RUN rm -rf /usr/share/nginx/html/*
RUN rm /etc/nginx/conf.d/default.conf

# Copia la tua configurazione personalizzata
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia i file statici dalla cartella dist
COPY dist/ /usr/share/nginx/html/

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]