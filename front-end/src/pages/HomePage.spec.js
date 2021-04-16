import React from 'react';
import { render } from '@testing-library/react';
import HomePage from './HomePage';


describe('HomePage', () => {
    it('has div with test id homepage', () => {
        // given
        const { queryByTestId } = render(<HomePage />);
        // when
        const homeDiv = queryByTestId('homepage');
        // then
        expect(homeDiv).toBeInTheDocument();
    });
})