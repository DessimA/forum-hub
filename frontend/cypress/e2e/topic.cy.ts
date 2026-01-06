describe('Topic Management', () => {
  beforeEach(() => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
  })

  it('should view topic list', () => {
    cy.contains('h1', 'Explorar Tópicos').should('be.visible')
    cy.get('[data-cy="topic-card"]').should('have.length.at.least', 1)
  })

  it('should open new topic dialog', () => {
    cy.get('[data-cy="nav-new-topic"]').should('be.visible').click({ force: true })
    cy.get('.v-dialog').should('be.visible')
    cy.contains('.v-toolbar-title', 'Novo Tópico').should('be.visible')
  })
})
