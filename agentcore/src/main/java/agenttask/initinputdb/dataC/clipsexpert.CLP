(deftemplate inputdata
    (slot paramname)
    (slot paramvalue)
)

(deftemplate outputdata
    (slot paramname)
    (slot paramvalue)
)


;для теста просто пропишем выходные данные
(deffacts outputdatafacts
	(outputdata (paramname occupancyC) (paramvalue 25))
	(outputdata (paramname conditionC) (paramvalue 25))
	(outputdata (paramname condition_d3) (paramvalue 25))
	(outputdata (paramname occupancy_d3) (paramvalue 25))
)