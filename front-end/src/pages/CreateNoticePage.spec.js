import React from 'react';
import { render } from '@testing-library/react';
import CreateNoticePage from './CreateNoticePage';


describe('CreateNoticePage', () => {
    it('has div with test id createnoticepage', () => {
        // given
        const { queryByTestId } = render(<CreateNoticePage />);
        // when
        const createNoticeDiv = queryByTestId('createnoticepage');
        // then
        expect(createNoticeDiv).toBeInTheDocument();
    });
});