;(defglobal ?*target* = gui) ; console, cgi, or gui

(deftemplate testdef
    (slot testcolor)
    (slot testname)
)

(defrule testdefrule
    (testdef (testcolor c) (testname n))
    =>
    (assert (testdef
                (testcolor c1)
                (testname n1)
            )
    )
)

(defrule personrule
    (addpersons add)
    =>
    ;(printout t "RR" crlf)
    (assert (addpersons add3))
)

(defrule personrule2
    (addpersons add3)
    =>
    ;(printout t "RR" crlf)
    (assert (addpersons add4))
)