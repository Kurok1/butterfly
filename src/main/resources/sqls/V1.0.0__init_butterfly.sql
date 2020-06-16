
CREATE TABLE user (
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    password varchar(50) NOT NULL,
    createdBy varchar(50) NULL,
    created timestamp NULL,
    lastUpdatedBy varchar(50) NULL,
    lastUpdated timestamp NULL,
    PRIMARY KEY(id)
);


CREATE TABLE datasource_config (
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    driverClass varchar(50) NOT NULL,
    url varchar(50) NOT NULL,
    user varchar(50) NOT NULL,
    password varchar(50) NOT NULL,
    description varchar(50) NULL,
    createdBy varchar(50) NULL,
    created timestamp NULL,
    lastUpdatedBy varchar(50) NULL,
    lastUpdated timestamp NULL,
    PRIMARY KEY(id)
);

CREATE TABLE node (
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    nodeType varchar(50) NOT NULL,
    config varchar(50) NOT NULL,
    nodeOrder int NOT NULL,
    executorId varchar(50) NOT NULL,
    nodeId int NOT NULL,
    createdBy varchar(50) NULL,
    created timestamp NULL,
    lastUpdatedBy varchar(50) NULL,
    lastUpdated timestamp NULL,
    PRIMARY KEY(id)
);

CREATE TABLE app (
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    key varchar(50) NOT NULL,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    enableSecret int NULL,
    createdBy varchar(50) NULL,
    created timestamp NULL,
    lastUpdatedBy varchar(50) NULL,
    lastUpdated timestamp NULL,
    PRIMARY KEY(id)
);

CREATE TABLE http_request_config (
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    url varchar(50) NOT NULL,
    method varchar(50) NOT NULL,
    requestFormat varchar(50) NOT NULL,
    responseFormat varchar(50) NOT NULL,
    description varchar(50) NULL,
    createdBy varchar(50) NULL,
    created timestamp NULL,
    lastUpdatedBy varchar(50) NULL,
    lastUpdated timestamp NULL,
    PRIMARY KEY(id)
);

CREATE TABLE xslt_definition (
    id int NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    code varchar(50) NOT NULL,
    name varchar(50) NOT NULL,
    xsltValue varchar(50) NOT NULL,
    createdBy varchar(50) NULL,
    created timestamp NULL,
    lastUpdatedBy varchar(50) NULL,
    lastUpdated timestamp NULL,
    PRIMARY KEY(id)
);