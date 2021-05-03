import axios from 'axios';
import * as apiCalls from './apiCalls';

describe('apiCalls', () => {
    describe('register', () => {
        it('calls /api/1.0/users', () => {
            // given
            const mockRegister = jest.fn();
            axios.post = mockRegister;
            // when
            apiCalls.register();
            // then
            const calledPath = mockRegister.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users');
        });
    });

    describe('updateUser', () => {
        it('calls /api/1.0/users/123 when the 123 is provided for the function', () => {
            // given
            const id = '123';
            const mockUpdateUser = jest.fn();
            axios.patch = mockUpdateUser;
            // when
            apiCalls.updateUser(id);
            // then
            const calledPath = mockUpdateUser.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users/123');
        });
    });


    describe('Login', () => {
        it('calls /api/1.0/login', () => {
            // given
            const mockLogin = jest.fn();
            axios.post = mockLogin;
            // when
            apiCalls.login({username: 'username', password: 'Password123'});
            // then
            const calledPath = mockLogin.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/login');
        });
    });

    describe('getUser', () => {
        it('calls /api/1.0/users/username123 when the username123 is provided for the function', () => {
            // given
            const username = 'username123';
            const mockGetUser = jest.fn();
            axios.get = mockGetUser;
            // when
            apiCalls.getUser(username);
            // then
            const calledPath = mockGetUser.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/users/username123');
        });
    });

    describe('postNotice', () => {
        it('calls /api/1.0/notices', () => {
            // given
            const mockPostNotice = jest.fn();
            axios.post = mockPostNotice;
            // when
            apiCalls.postNotice({});
            // then
            const calledPath = mockPostNotice.mock.calls[0][0];
            expect(calledPath).toBe('/api/1.0/notices');
        });
    });
});