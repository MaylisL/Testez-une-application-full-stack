import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';

import { MeComponent } from './me.component';
import { Router } from '@angular/router';
import { UserService } from 'src/app/services/user.service';

import { of } from 'rxjs';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let mockUserService: Partial<UserService>;
  let mockSessionService: Partial<SessionService>;
  let mockMatSnackBar: Partial<MatSnackBar>;
  let mockRouter: Partial<Router>;
  let spyWindowHistoryBack: jest.SpyInstance;



  beforeEach(async () => {

    spyWindowHistoryBack = jest.spyOn(window.history, 'back').mockImplementation(() => { });

    mockUserService = {
      delete: jest.fn().mockReturnValue(of({})),
      getById: jest.fn().mockReturnValue(of({})),
    };

    mockSessionService = {
      sessionInformation: { id: 1, admin: true } as SessionInformation,
      logOut: jest.fn()
    };

    mockMatSnackBar = {
      open: jest.fn()
    };

    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatCardModule,
        MatSnackBarModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule
      ],
      providers: [
        { provide: UserService, useValue: mockUserService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    spyWindowHistoryBack.mockRestore();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get user depending on his id', () => {
    component.ngOnInit();

    expect(mockUserService.getById).toHaveBeenCalledWith('1');
  });

  it('should call window.history.back() when back() is called', () => {
    component.back();

    expect(spyWindowHistoryBack).toHaveBeenCalled();
    spyWindowHistoryBack.mockRestore();
  });

  it('should delete the account and redirect', () => {
    component.delete();

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

});
