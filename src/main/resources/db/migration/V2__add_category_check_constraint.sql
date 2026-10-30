ALTER TABLE food_item
ADD CONSTRAINT chk_food_item_categoria CHECK (
    categoria IN (
        'CEREAIS','TUBERCULOS','LEGUMINOSAS','VERDURAS','LEGUMES',
        'FRUTAS','PROTEINAS','GORDURAS','DOCES','BEBIDAS','TEMPEROS'
    )
);