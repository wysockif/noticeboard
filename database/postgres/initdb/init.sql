CREATE ROLE noticeboard_user WITH
    LOGIN
    PASSWORD 'Cj1dNIdO4JjRoNXe46yzF7dyokDJ'
    NOSUPERUSER
    INHERIT
    CREATEDB
    NOCREATEROLE
    NOREPLICATION;

CREATE DATABASE noticeboard_database WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    LC_COLLATE = 'pl_PL.UTF-8'
    LC_CTYPE = 'pl_PL.UTF-8';

CREATE SCHEMA IF NOT EXISTS noticeboard;