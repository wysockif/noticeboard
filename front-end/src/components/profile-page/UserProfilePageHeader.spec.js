import React from 'react';
import {render} from '@testing-library/react';
import {MemoryRouter} from "react-router-dom";
import UserProfilePageHeader from "./UserProfilePageHeader";


const renderProfilePageHeaderInRouter = props => {
    return render(
        <MemoryRouter>
            <UserProfilePageHeader {...props}/>
        </MemoryRouter>
    );
};

describe('UserProfilePageHeader', () => {
    it('displays the collapse button when canBeModified is true', () => {
        // given
        const {queryByText} = renderProfilePageHeaderInRouter({canBeModified: true});
        // when
        const collapseButton = queryByText('Pokaż opcje edycji');
        // then
        expect(collapseButton).toBeInTheDocument();
    });

    it('does not display the collapse button when there is no canBeModified in props', () => {
        // given
        const {queryByText} = renderProfilePageHeaderInRouter();
        // when
        const collapseButton = queryByText('Pokaż opcje edycji');
        // then
        expect(collapseButton).not.toBeInTheDocument();
    });
})