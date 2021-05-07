import React from 'react';
import {render} from '@testing-library/react';
import EditNoticePage from './EditNoticePage';
import {Provider} from "react-redux";
import {MemoryRouter} from "react-router-dom";
import {createStore} from "redux";
import authenticationReducer from "../redux/authenticationReducer";


describe('EditNoticePage', () => {
    it('has div with test id editnoticepage', () => {
        // given
        let store = createStore(authenticationReducer);
        const {queryByTestId} = render(
            <Provider store={store}>
                <MemoryRouter>
                    <EditNoticePage/>
                </MemoryRouter>
            </Provider>
        );
        // when
        const noticeformDiv = queryByTestId('editnoticepage');
        // then
        expect(noticeformDiv).toBeInTheDocument();
    });
})