IF DB_ID(N'dbsiste') IS NULL
BEGIN
	CREATE DATABASE dbsiste;
END
GO

USE dbsiste;
GO

IF OBJECT_ID(N'dbo.usuarios', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.usuarios (
		id_usuario INT IDENTITY(1,1) NOT NULL,
		nombre NVARCHAR(100) NOT NULL,
		apellido NVARCHAR(100) NOT NULL,
		email NVARCHAR(150) NOT NULL,
		telefono NVARCHAR(20) NULL,
		tipo_documento NVARCHAR(30) NOT NULL,
		numero_documento NVARCHAR(30) NOT NULL,
		direccion NVARCHAR(250) NULL,
		password NVARCHAR(255) NOT NULL DEFAULT '123456',
		estado BIT NOT NULL CONSTRAINT df_usuarios_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_usuarios_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
		CONSTRAINT uq_usuarios_email UNIQUE (email),
		CONSTRAINT uq_usuarios_numero_documento UNIQUE (numero_documento)
	);
END
GO

IF OBJECT_ID(N'dbo.categorias', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.categorias (
		id_categoria INT IDENTITY(1,1) NOT NULL,
		nombre NVARCHAR(120) NOT NULL,
		descripcion NVARCHAR(255) NULL,
		estado BIT NOT NULL CONSTRAINT df_categorias_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_categorias_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_categorias PRIMARY KEY (id_categoria),
		CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
	);
END
GO

IF OBJECT_ID(N'dbo.cultivos', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.cultivos (
		id_cultivo INT IDENTITY(1,1) NOT NULL,
		nombre NVARCHAR(120) NOT NULL,
		descripcion NVARCHAR(255) NULL,
		unidad_medida NVARCHAR(20) NOT NULL,
		estado BIT NOT NULL CONSTRAINT df_cultivos_estado DEFAULT (1),
		id_categoria INT NOT NULL,
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_cultivos_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_cultivos PRIMARY KEY (id_cultivo),
		CONSTRAINT uq_cultivos_nombre_categoria UNIQUE (nombre, id_categoria),
		CONSTRAINT ck_cultivos_unidad_medida CHECK (unidad_medida IN (N'kg', N't')),
		CONSTRAINT fk_cultivos_categoria FOREIGN KEY (id_categoria) REFERENCES dbo.categorias (id_categoria)
	);
END
GO

IF OBJECT_ID(N'dbo.insumos', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.insumos (
		id_insumo INT IDENTITY(1,1) NOT NULL,
		nombre NVARCHAR(120) NOT NULL,
		tipo_insumo NVARCHAR(100) NOT NULL,
		unidad_medida NVARCHAR(20) NOT NULL,
		proveedor NVARCHAR(150) NULL,
		costo DECIMAL(12,2) NOT NULL CONSTRAINT df_insumos_costo DEFAULT (0),
		cantidad DECIMAL(12,3) NOT NULL CONSTRAINT df_insumos_cantidad DEFAULT (0),
		estado BIT NOT NULL CONSTRAINT df_insumos_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_insumos_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_insumos PRIMARY KEY (id_insumo),
		CONSTRAINT ck_insumos_unidad_medida CHECK (unidad_medida IN (N'gramos', N'litros', N'unidad'))
	);
END
GO

IF OBJECT_ID(N'dbo.parcelas', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.parcelas (
		id_parcela INT IDENTITY(1,1) NOT NULL,
		nombre NVARCHAR(120) NOT NULL,
		direccion_geografica NVARCHAR(250) NULL,
		disponibilidad BIT NOT NULL CONSTRAINT df_parcelas_disponibilidad DEFAULT (1),
		area DECIMAL(12,2) NOT NULL CONSTRAINT df_parcelas_area DEFAULT (0),
		observaciones NVARCHAR(500) NULL,
		id_cultivo INT NULL,
		id_seguimiento_actual INT NULL,
		id_usuario INT NULL,
		id_insumo INT NULL,
		estado BIT NOT NULL CONSTRAINT df_parcelas_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_parcelas_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_parcelas PRIMARY KEY (id_parcela),
		CONSTRAINT fk_parcelas_cultivo FOREIGN KEY (id_cultivo) REFERENCES dbo.cultivos (id_cultivo),
		CONSTRAINT fk_parcelas_usuario FOREIGN KEY (id_usuario) REFERENCES dbo.usuarios (id_usuario),
		CONSTRAINT fk_parcelas_insumo FOREIGN KEY (id_insumo) REFERENCES dbo.insumos (id_insumo)
	);
END
GO

IF OBJECT_ID(N'dbo.seguimiento_cultivo', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.seguimiento_cultivo (
		id_seguimiento INT IDENTITY(1,1) NOT NULL,
		nombre NVARCHAR(120) NOT NULL,
		descripcion NVARCHAR(255) NULL,
		observaciones NVARCHAR(500) NULL,
		nota DECIMAL(5,2) NULL,
		fecha_hora DATETIME2(0) NOT NULL CONSTRAINT df_seguimiento_fecha_hora DEFAULT (SYSDATETIME()),
		cita_programada DATETIME2(0) NULL,
		id_parcela INT NOT NULL,
		estado BIT NOT NULL CONSTRAINT df_seguimiento_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_seguimiento_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_seguimiento_cultivo PRIMARY KEY (id_seguimiento),
		CONSTRAINT fk_seguimiento_parcela FOREIGN KEY (id_parcela) REFERENCES dbo.parcelas (id_parcela),
		CONSTRAINT ck_seguimiento_nota CHECK (nota IS NULL OR (nota >= 0 AND nota <= 20))
	);
END
GO

IF COL_LENGTH(N'dbo.parcelas', N'id_seguimiento_actual') IS NOT NULL
AND NOT EXISTS (
	SELECT 1
	FROM sys.foreign_keys
	WHERE name = N'fk_parcelas_seguimiento_actual'
)
BEGIN
	ALTER TABLE dbo.parcelas
	ADD CONSTRAINT fk_parcelas_seguimiento_actual
		FOREIGN KEY (id_seguimiento_actual) REFERENCES dbo.seguimiento_cultivo (id_seguimiento);
END
GO

IF OBJECT_ID(N'dbo.reportes', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.reportes (
		id_reporte INT IDENTITY(1,1) NOT NULL,
		nombre_reporte NVARCHAR(150) NOT NULL,
		id_parcela INT NOT NULL,
		estado BIT NOT NULL CONSTRAINT df_reportes_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_reportes_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_reportes PRIMARY KEY (id_reporte),
		CONSTRAINT fk_reportes_parcela FOREIGN KEY (id_parcela) REFERENCES dbo.parcelas (id_parcela)
	);
END
GO


IF OBJECT_ID(N'dbo.riegos', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.riegos (
		id_riego INT IDENTITY(1,1) NOT NULL,
		id_parcela INT NOT NULL,
		fecha_riego DATETIME2(0) NOT NULL,
		tipo_riego NVARCHAR(50) NOT NULL, -- 'manual', 'aspersion', 'goteo', 'inundacion'
		duracion_minutos INT NOT NULL DEFAULT 0,
		cantidad_agua DECIMAL(10,2) NULL, -- litros o m3
		responsable NVARCHAR(100) NULL,
		observaciones NVARCHAR(500) NULL,
		estado BIT NOT NULL CONSTRAINT df_riegos_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_riegos_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_riegos PRIMARY KEY (id_riego),
		CONSTRAINT fk_riegos_parcela FOREIGN KEY (id_parcela) REFERENCES dbo.parcelas (id_parcela),
		CONSTRAINT ck_riegos_tipo CHECK (tipo_riego IN ('manual', 'aspersion', 'goteo', 'inundacion'))
	);
END
GO


IF OBJECT_ID(N'dbo.cosechas', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.cosechas (
		id_cosecha INT IDENTITY(1,1) NOT NULL,
		id_parcela INT NOT NULL,
		id_cultivo INT NOT NULL,
		fecha_cosecha DATETIME2(0) NOT NULL,
		cantidad DECIMAL(12,3) NOT NULL, -- kg o toneladas
		unidad_medida NVARCHAR(20) NOT NULL, -- 'kg', 't', 'unidades'
		calidad NVARCHAR(50) NOT NULL, -- 'excelente', 'buena', 'regular', 'mala'
		precio_venta DECIMAL(10,2) NULL, -- precio por unidad/kg
		ingreso_total DECIMAL(12,2) NULL, -- cantidad * precio_venta
		observaciones NVARCHAR(500) NULL,
		estado BIT NOT NULL CONSTRAINT df_cosechas_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_cosechas_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_cosechas PRIMARY KEY (id_cosecha),
		CONSTRAINT fk_cosechas_parcela FOREIGN KEY (id_parcela) REFERENCES dbo.parcelas (id_parcela),
		CONSTRAINT fk_cosechas_cultivo FOREIGN KEY (id_cultivo) REFERENCES dbo.cultivos (id_cultivo),
		CONSTRAINT ck_cosechas_calidad CHECK (calidad IN ('excelente', 'buena', 'regular', 'mala')),
		CONSTRAINT ck_cosechas_unidad CHECK (unidad_medida IN ('kg', 't', 'unidades'))
	);
END
GO


IF OBJECT_ID(N'dbo.programacion_riegos', N'U') IS NULL
BEGIN
	CREATE TABLE dbo.programacion_riegos (
		id_programacion INT IDENTITY(1,1) NOT NULL,
		id_parcela INT NOT NULL,
		fecha_programada DATETIME2(0) NOT NULL,
		frecuencia NVARCHAR(30) NOT NULL, -- 'diario', 'semanal', 'quincenal', 'mensual'
		tipo_riego NVARCHAR(50) NOT NULL,
		duracion_estimada INT NOT NULL DEFAULT 0,
		activo BIT NOT NULL CONSTRAINT df_programacion_activo DEFAULT (1),
		observaciones NVARCHAR(500) NULL,
		estado BIT NOT NULL CONSTRAINT df_programacion_estado DEFAULT (1),
		created_at DATETIME2(0) NOT NULL CONSTRAINT df_programacion_created_at DEFAULT (SYSDATETIME()),
		updated_at DATETIME2(0) NULL,
		deleted_at DATETIME2(0) NULL,
		restored_at DATETIME2(0) NULL,
		CONSTRAINT pk_programacion_riegos PRIMARY KEY (id_programacion),
		CONSTRAINT fk_programacion_parcela FOREIGN KEY (id_parcela) REFERENCES dbo.parcelas (id_parcela)
	);
END
GO