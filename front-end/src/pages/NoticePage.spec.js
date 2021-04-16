import React from 'react';
import { render } from '@testing-library/react';
import NoticePage from './NoticePage';


describe('NoticePage', () => {
    it('has div with test id noticepage', () => {
        // given
        const { queryByTestId } = render(<NoticePage />);
        // when
        const noticeDiv = queryByTestId('noticepage');
        // then
        expect(noticeDiv).toBeInTheDocument();
    });
})