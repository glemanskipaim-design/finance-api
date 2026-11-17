-- Esquema do banco "financeiro" (PostgreSQL).
-- O docker-compose.yml roda este script sozinho na primeira subida do banco.
-- Sem Docker: crie o banco "financeiro" e rode este arquivo nele (psql ou pgAdmin).

CREATE TABLE IF NOT EXISTS "usuario" (
	"id" serial NOT NULL UNIQUE,
	"nome" varchar(150) NOT NULL,
	"email" varchar(150) NOT NULL UNIQUE,
	"senhahash" varchar(100) NOT NULL,
	PRIMARY KEY ("id")
);
CREATE TABLE IF NOT EXISTS "categoria" (
	"id" serial NOT NULL UNIQUE,
	"descricao" varchar(150) NOT NULL,
	"usuario_id" integer NOT NULL,
	PRIMARY KEY ("id")
);
CREATE TABLE IF NOT EXISTS "conta" (
	"id" serial NOT NULL UNIQUE,
	"descricao" varchar(150) NOT NULL,
	"usuario_id" integer NOT NULL,
	PRIMARY KEY ("id")
);
CREATE TABLE IF NOT EXISTS "transacao" (
	"id" serial NOT NULL UNIQUE,
	"descricao" varchar(500) NOT NULL,
	"valor" numeric(12,2) NOT NULL CHECK ("valor" > 0),
	"tipo" varchar(1) NOT NULL CHECK ("tipo" IN ('R', 'D')),
	"datatransacao" date NOT NULL,
	"conta_id" integer NOT NULL,
	"categoria_id" integer,          -- nulo nas transações de transferência
	"transferencia" varchar(36),     -- liga as duas pernas de uma transferência
	PRIMARY KEY ("id")
);
ALTER TABLE "categoria" ADD CONSTRAINT "categoria_fk_usuario" FOREIGN KEY ("usuario_id") REFERENCES "usuario"("id");
ALTER TABLE "conta" ADD CONSTRAINT "conta_fk_usuario" FOREIGN KEY ("usuario_id") REFERENCES "usuario"("id");
ALTER TABLE "transacao" ADD CONSTRAINT "transacao_fk5" FOREIGN KEY ("conta_id") REFERENCES "conta"("id");
ALTER TABLE "transacao" ADD CONSTRAINT "transacao_fk6" FOREIGN KEY ("categoria_id") REFERENCES "categoria"("id");
CREATE INDEX IF NOT EXISTS "transacao_conta_idx" ON "transacao" ("conta_id");
CREATE INDEX IF NOT EXISTS "transacao_categoria_idx" ON "transacao" ("categoria_id");
CREATE INDEX IF NOT EXISTS "transacao_transferencia_idx" ON "transacao" ("transferencia");
