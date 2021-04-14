import React from 'react';
import { queryByPlaceholderText, render } from '@testing-library/react';
import { LoginPage } from './LoginPage';
import '@testing-library/jest-dom/extend-expect';



describe('LoginPage', () => {
    describe('Layout', () => {

        it('has login header', () => {
            // given
            const { container } = render(<LoginPage />);
            // when
            const header = container.querySelector('h1');
            // then
            expect(header).toHaveTextContent('Zaloguj się');
        });

      it('has input for username', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            // when
            const input = queryByPlaceholderText('Nazwa użytkownika')
            // then
            expect(input).toBeInTheDocument();
        });

        it('has input for password', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            // when
            const input = queryByPlaceholderText('Hasło')
            // then
            expect(input).toBeInTheDocument();
        });

        it('has password type for input for password', () => {
            // given
            const { queryByPlaceholderText } = render(<LoginPage />);
            // when
            const passwordInput = queryByPlaceholderText('Hasło');
            // then
            expect(passwordInput.type).toBe('password');
        });


        it('has login button', () => {
            // given
            const { container } = render(<LoginPage />);
            // when
            const header = container.querySelector('button');
            // then
            expect(header).toHaveTextContent('Zaloguj się');
        });
    
    });
});

console.error = () => {}