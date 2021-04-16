import React from 'react';
import { render } from '@testing-library/react';
import TopBar from './TopBar';
import { MemoryRouter } from 'react-router';

describe('TopBar', () => {
    describe('Layout', () => {
        const renderTopbar = () => {
            return render(
                <MemoryRouter>
                    <TopBar />
                </MemoryRouter>
            );
        };

        it('has link to home', () => {
            // given
            const { container } = renderTopbar();
            // when
            const image = container.querySelector('img');
            // then
            const link = image.parentElement;
            expect(link).toHaveAttribute('href', '/')
        });

        it('has app logo', () => {
            // given
            const { container } = renderTopbar();
            // when
            const image = container.querySelector('img');
            // then
            expect(image.src).toContain('logo.png');
        });

        it('has link to register', () => {
            // given
            const { queryByText } = renderTopbar();
            // when
            const registerLink = queryByText('Rejestracja');
            // then
            expect(registerLink.getAttribute('href')).toBe('/register')
        });
    });
});