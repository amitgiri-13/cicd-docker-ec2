from fastapi import FastAPI, Request, Depends, Form
from fastapi.responses import RedirectResponse
from fastapi.templating import Jinja2Templates
from sqlalchemy.orm import Session

from .database import Base, engine, SessionLocal
from .models import Member

Base.metadata.create_all(bind=engine)

app = FastAPI()
templates = Jinja2Templates(directory="app/templates")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.get("/")
def read_members(request: Request, db: Session = Depends(get_db)):
    members = db.query(Member).all()
    return templates.TemplateResponse(
        "index.html",
        {"request": request, "members": members}
    )


@app.get("/add")
def add_member_form(request: Request):
    return templates.TemplateResponse("add_member.html", {"request": request})


@app.post("/add")
def add_member(
    name: str = Form(...),
    role: str = Form(...),
    db: Session = Depends(get_db),
):
    member = Member(name=name, role=role)
    db.add(member)
    db.commit()
    return RedirectResponse("/", status_code=303)


@app.get("/edit/{member_id}")
def edit_member_form(member_id: int, request: Request, db: Session = Depends(get_db)):
    member = db.query(Member).get(member_id)
    return templates.TemplateResponse(
        "edit_member.html",
        {"request": request, "member": member}
    )


@app.post("/edit/{member_id}")
def edit_member(
    member_id: int,
    name: str = Form(...),
    role: str = Form(...),
    db: Session = Depends(get_db),
):
    member = db.query(Member).get(member_id)
    member.name = name
    member.role = role
    db.commit()
    return RedirectResponse("/", status_code=303)


@app.get("/delete/{member_id}")
def delete_member(member_id: int, db: Session = Depends(get_db)):
    member = db.query(Member).get(member_id)
    db.delete(member)
    db.commit()
    return RedirectResponse("/", status_code=303)
