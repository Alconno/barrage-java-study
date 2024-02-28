CREATE DATABASE account;
CREATE DATABASE merchant;
CREATE DATABASE notification;
CREATE DATABASE payment;
CREATE DATABASE travel;
CREATE DATABASE keycloak;

CREATE USER uaccount PASSWORD 'uaccount';
CREATE USER umerchant PASSWORD 'umerchant';
CREATE USER unotification PASSWORD 'unotification';
CREATE USER upayment PASSWORD 'upayment';
CREATE USER utravel PASSWORD 'utravel';
CREATE USER ukeycloak PASSWORD 'ukeycloak';

GRANT ALL PRIVILEGES ON DATABASE "account" TO uaccount;
GRANT ALL PRIVILEGES ON DATABASE "merchant" TO umerchant;
GRANT ALL PRIVILEGES ON DATABASE "notification" TO unotification;
GRANT ALL PRIVILEGES ON DATABASE "payment" TO upayment;
GRANT ALL PRIVILEGES ON DATABASE "travel" TO utravel;
GRANT ALL PRIVILEGES ON DATABASE "keycloak" TO "ukeycloak";

\c account
GRANT ALL ON SCHEMA public TO uaccount;
\c merchant
GRANT ALL ON SCHEMA public TO umerchant;
\c notification
GRANT ALL ON SCHEMA public TO unotification;
\c payment
GRANT ALL ON SCHEMA public TO upayment;
\c travel
GRANT ALL ON SCHEMA public TO utravel;
\c keycloak
GRANT ALL ON SCHEMA public TO ukeycloak;