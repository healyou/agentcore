(deftemplate inputdata
    (slot paramname)
    (slot paramvalue)
)

(deftemplate outputdata
    (slot paramvalue)
)


;для теста просто пропишем выходные данные
(deffacts outputdatafacts
	(outputdata (paramvalue 25))
)
