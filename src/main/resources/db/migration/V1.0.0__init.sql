create extension if not exists pgcrypto;

drop table if exists "posts";
create table if not exists "posts"
(
  "id"         uuid      default gen_random_uuid(),
  "title"      text,
  "content"    text,
  "created_at" timestamp default now()
);
