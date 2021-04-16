import React from 'react';
import { render } from '@testing-library/react';
import NoticeFormPage from './NoticeFormPage';


describe('NoticeFormPage', () => {
    it('has div with test id noticeformpage', () => {
        // given
        const { queryByTestId } = render(<NoticeFormPage />);
        // when
        const noticeformDiv = queryByTestId('noticeformpage');
        // then
        expect(noticeformDiv).toBeInTheDocument();
    });
})