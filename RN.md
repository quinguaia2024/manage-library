*RN01* 
_Borrow_
1. the service should be able to identify whether is a *STUDENT* or *TEACHER* which both of them are READER's.
STUDENT -> Only 7 days maximium book 3 at once.
TEACHER -> Only 14 days maximium book 5 at once. 
2. Before a new borrow the System must check the READER has an *Debt* if yes block the action.

*RN02*
_FINE & BOOKRETURN_
The calculation of the fine must be executed when the book is being returned that mean if the returning date passes the Date set the function is called *CalCFine()*=> (borrowingDate - returnigDate) * ValuePerDay. -> ChronoUnit.DAYS.between(dataPrevista, dataDevolucao) - *to calculate the difference of the days* from *java.time*

*RN03*
_FIFO (First-In, First-Out)_
in case there's no book to be borrowed that mean you going to *Reservelist*.
if we have a reserve on specific book that means it cannot be reborrowed.

*When to Execute*
When the book is being returned the system search for that specific book if there's a reselve active if it is found the query must be attached with order by reserveDate *ASC* changing the status of the book to *Reserved* not *available* meaning the that reserve found status should be changed to *WaitingTobePickeup* when the READER come to take it is changed to *ATTENDED*  


_TODO_
1. Calculte if the book is available
2. Extend READER to TEACHER and STUDENT


*UNIT TEST*
Cenário de Teste                                  Ação                           Resultado Esperado                      Resultado Obtido                Status
CT01 -Usuário ativo, sem Empréstimo
O sistema
Empréstimomultas, livroregistrado, status
registrou e
Válidodisponível.do livro muda para atualizou o status
Aprovado
EMPRESTADO. corretamente.