-- name: product
select *
from product;

-- name: productWithParams
select * from product where code = :code;

-- name: productNames
select name poductName from product;