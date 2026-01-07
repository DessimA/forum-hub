describe('Theme Toggle', () => {
  it('should toggle theme and persist after reload', () => {
    cy.visit('/login')
    cy.contains('Acesse sua conta para participar da comunidade', { timeout: 15000 }).should('be.visible')
    
    // Get initial state
    cy.get('.auth-submit-btn').then(($btn) => {
      const initialColor = $btn.css('background-color')
      // Detect initial theme: Dark (Sky 400) or Light (Sky 700)
      const isDarkInitial = initialColor === 'rgb(56, 189, 248)'
      
      const targetColor = isDarkInitial ? 'rgb(2, 132, 199)' : 'rgb(56, 189, 248)'
      const targetIcon = isDarkInitial ? 'mdi-moon-waning-crescent' : 'mdi-white-balance-sunny'

      // Toggle theme
      cy.get('[data-cy="theme-toggle"]').should('be.visible').click({ force: true })
      
      // Verify change with callback for stability during transitions
      cy.get('.auth-submit-btn').should(($newBtn) => {
        expect($newBtn.css('background-color')).to.equal(targetColor)
      })
      
      cy.get('[data-cy="theme-toggle"]').find('i').should('have.class', targetIcon)
      
      // Check persistence after reload
      cy.reload()
      cy.contains('Acesse sua conta para participar da comunidade').should('be.visible')
      cy.get('.auth-submit-btn', { timeout: 20000 }).should('have.css', 'background-color', targetColor)
    })
  })
})