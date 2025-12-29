describe('Topic Management', () => {
  beforeEach(() => {
    cy.visit('/login')
    cy.get('input[type="text"]').type('admin@forumhub.com')
    cy.get('input[type="password"]').type('123456')
    cy.get('button[type="submit"]').click()
  })

  it('should view topic list', () => {
    cy.visit('/')
    cy.contains('Explorar Tópicos')
    // Check if list is populated (assuming backend has data)
    // cy.get('.topico-card').should('have.length.gt', 0)
  })

  it('should open new topic dialog', () => {
    cy.contains('Novo Tópico').click()
    cy.contains('Novo Tópico').should('be.visible')
    cy.contains('Postar Tópico').should('be.disabled') // Form invalid initially
  })
})
