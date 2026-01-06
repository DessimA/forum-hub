describe('Mobile Topic Cards', () => {
  beforeEach(() => {
    cy.viewport(375, 667)
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
  })
  
  it('cards should occupy full width', () => {
    cy.visit('/topics')
    cy.get('[data-cy="topic-card"]').first().invoke('width').should('be.gt', 250)
  })
  
  it('title should truncate at 2 lines', () => {
    cy.get('[data-cy="topic-title"]').first()
      .should('have.css', '-webkit-line-clamp', '2')
  })
})
