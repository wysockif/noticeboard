import React from 'react';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import ErrorPage from './ErrorPage';


describe('ErrorPage', () => {
    const renderErrorPage = () => {
        return render(
            <MemoryRouter>
                <ErrorPage/>
            </MemoryRouter>
        );
    }

    it('has div with test id homepage', () => {
        // given
        const {queryByTestId} = renderErrorPage();
        // when
        const errorDiv = queryByTestId('errorpage');
        // then
        expect(errorDiv).toBeInTheDocument();
    });

    it('has link', () => {
        // given
        const {container} = renderErrorPage();
        // when
        const link = container.querySelector('a');
        // then
        expect(link).toBeInTheDocument();
    });

    it('has link to home page', () => {
        // given
        const {container} = renderErrorPage();
        // when
        const link = container.querySelector('a');
        // then
        expect(link).toHaveAttribute('href', '/');
    });
})
console.error = () => {
}