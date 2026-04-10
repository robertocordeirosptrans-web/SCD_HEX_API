SELECT *
FROM all_objects@DBLINK_SCA
WHERE object_type = 'PACKAGE'
ORDER BY object_name;


SELECT argument_name, data_type, in_out
FROM all_arguments
WHERE package_name = 'PKG_EMPRESA_LIMINAR'
ORDER BY sequence;