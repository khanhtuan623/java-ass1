import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Test {
	private static List<String> listStr;

	private static List<SinhVien> listStudent;
	private static List<GiangVien> listTeacher;
	private static List<MonHoc> listSubject;

	private static List<MonHoc> listSubjectOfStudent;// List of subjects studied
														// by students
	private static List<SinhVien> listStudentsOfSubject; // List of students
															// studied by
	private static List<MonHoc> listSubjectOfTeacher;// List of teaching
														// subjects

	private static SinhVien sinhvien;
	private static GiangVien giangvien;

	private static File fileStudent = new File("students.dat");
	private static File fileTeacher = new File("teachers.dat");
	private static File file4d = new File("4d.dat");
	private static File file4e = new File("4e.dat");
	private static File file4f = new File("4f.dat");

	private static PrintWriter printStudent;
	private static PrintWriter printTeacher;
	private static PrintWriter print4d;
	private static PrintWriter print4e;
	private static PrintWriter print4f;
	private static String[] arr = null;

	public static void main(String[] args) {
		fileIn();
		fileOut();
		System.out.println("List Students: " + listStudent.size());
		System.out.println("List Teachers: " + listTeacher.size());
		System.out.println("List Subjects: " + listSubject.size());
		System.out.println("List Subject of Student: " + listSubjectOfStudent.size());
		System.out.println("List Student of Subjects: " + listStudentsOfSubject.size());
		System.out.println("List Subject of Teacher: " + listSubjectOfTeacher.size());
	}

	public static void readData() throws FileNotFoundException {
		listTeacher = new ArrayList<>();
		listStudent = new ArrayList<>();
		listSubject = new ArrayList<MonHoc>();

		for (int i = 0; i < listStr.size(); i++) {
			int ht = listStr.get(i).lastIndexOf("\t");
			String hashtag = listStr.get(i).substring(0, ht);
			String body = listStr.get(i).substring(ht + 1);
			String[] data = body.split(",");
			listStr.remove("");
			if(hashtag.equals("")||body.equals("")||data.length<2||data.length>3||data[0].equals(null)||data[1].equals(null)){
				listStr.remove(i);
			}
			if (hashtag.equals("#GiangVien")) {
				if(data.length>3||data.length<3){
					listStr.remove(i);
				}
				GiangVien giangvien = new GiangVien(data[0], data[1], data[2].charAt(0));
				listTeacher.add(giangvien);
			}
			if (hashtag.equals("#MonHoc")) {
				if(data.length>3||data.length<3){
					listStr.remove(i);
				}
				MonHoc monhoc = new MonHoc(data[0], data[1], Integer.parseInt(data[2]));
				listSubject.add(monhoc);
			}
			if (hashtag.equals("#SinhVien")) {
				if(data.length>3||data.length<3){
					listStr.remove(i);
				}
				sinhvien = new SinhVien(data[0], data[1], data[2].charAt(0));
				listStudent.add(sinhvien);
			}
			if (hashtag.equals("#svTKB")) {
				if(data.length>2||data.length<2){
					listStr.remove(i);
				}
				for (SinhVien s : listStudent) {
					if (s.getId().equals(data[0])) {
						MonHoc h = new MonHoc();
						h.setMaMH(data[1]);
						s.addMonHoc(h);
					}
				}
			}
			if (hashtag.equals("#gvTKB")) {
				if(data.length>2||data.length<2){
					listStr.remove(i);
				}
				for (GiangVien gv : listTeacher) {
					if (gv.getId().equals(data[0])) {
						MonHoc mh = new MonHoc();
						mh.setMaMH(data[1]);
						gv.addMonHoc(mh);
					}
				}
			}
		}
	}
	/* Method Read File */
	public static void fileIn() {
		try {
			listStr = new ArrayList<String>();
			FileInputStream fis = new FileInputStream("input4.dat");
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line = br.readLine();
			while (line != null) {
				listStr.add(line);
				line = br.readLine();
				listStr.remove("");
			}
			System.out.println("List Original: " + listStr.size());
			bruteforce(listStr);
			arr = listStr.get(0).split(",");
			listStr.remove(0);
			for (int i = 0; i < listStr.size(); i++) {
				int ch = listStr.get(i).charAt(0);
				if (listStr.get(i).equals(""))
					listStr.remove("");
				if (ch != '#') {
					listStr.remove(i);
				}
			}
			System.out.println("List after remove: " + listStr.size());
			Collections.sort(listStr);
			br.close();
			fis.close();
			isr.close();
			readData();
			soTinChiTichLuy();
			soLopGiangDay();

		} catch (Exception e) {
			e.getMessage();
		}
	}

	/* Method print all file */
	public static void fileOut() {
		try {

			printStudent = new PrintWriter(fileStudent);
			printTeacher = new PrintWriter(fileTeacher);
			print4d = new PrintWriter(file4d);
			print4e = new PrintWriter(file4e);
			print4f = new PrintWriter(file4f);

			findSubjectbyIDStudent(arr[0]);
			findStudentsbyIdSubject(arr[1]);
			findSubjectbyIDTeacher(arr[2]);

			/* print file Students.dat */
			listStudent.stream().forEach(sv -> printStudent
					.println(sv.id + "," + sv.name + "," + sv.gender + "," + sv.getSoTinChiTichLuy()));
			/* print file Teacher.dat */
			listTeacher.stream().forEach(gv -> printTeacher
					.println(gv.getId() + "," + gv.getName() + "," + gv.getGender() + "," + gv.getSoLopGiangDay()));
			/* print file 4d.dat */
			listSubjectOfStudent.stream()
					.forEach(mh -> print4d.println(mh.getMaMH() + "," + mh.getTenMH() + "," + mh.getTinChi()));
			/* print file 4e.dat */
			listStudentsOfSubject.stream()
					.forEach(sv -> print4e.println(sv.getId() + "," + sv.name + "," + sv.getGender()));
			/* print file 4f.dat */
			listSubjectOfTeacher.stream()
					.forEach(mh -> print4f.println(mh.getMaMH() + "," + mh.getTenMH() + "," + mh.getTinChi()));
			System.out.println("Succeesfully");
		} catch (Exception e) {
			e.getMessage();
			System.out.println("UnSuccessfully");
		} finally {
			printStudent.flush();
			printStudent.close();
			printTeacher.flush();
			printStudent.close();
			print4d.flush();
			print4d.close();
			print4e.flush();
			print4e.close();
			print4f.flush();
			print4f.close();
		}

	}

	/* Method tinh so tin chi tich luy cua 1 sinh vien */
	public static void soTinChiTichLuy() {
		int count = 0;
		for (SinhVien s : listStudent) {
			count = 0;
			for (MonHoc mh : s.getSvTKB()) {
				for (MonHoc m : listSubject) {
					if (mh.getMaMH().equals(m.getMaMH()))
						count += m.getTinChi();
				}
			}
			s.setSoTinChiTichLuy(count);
		}
	}

	/* Method tinh so lop giang day cua mot giang vien */
	public static void soLopGiangDay() {
		int count = 0;
		for (GiangVien gv : listTeacher) {
			count = 0;
			for (MonHoc mh : gv.getGvTKB()) {
				for (MonHoc h : listSubject) {
					if (mh.getMaMH().equals(h.getMaMH()))
						count++;
				}
			}
			gv.setSoLopGiangDay(count);
		}
	}

	/* method find Subject Of Student by ID Student */
	public static List<MonHoc> findSubjectbyIDStudent(String id) {
		listSubjectOfStudent = new ArrayList<>();
		for (SinhVien sv : listStudent) {
			if (sv.getId().equals(id)) {
				for (MonHoc mh : sv.getSvTKB()) {
					for (MonHoc m : listSubject) {
						if (mh.getMaMH().equals(m.getMaMH())) {
							listSubjectOfStudent.add(m);
						}
					}
				}
			}
		}
		return (ArrayList<MonHoc>) listSubjectOfStudent;
	}

	/* method find Students Of Subject by ID Subject */
	public static List<SinhVien> findStudentsbyIdSubject(String id) {
		listStudentsOfSubject = new ArrayList<>();
		for (MonHoc mh : listSubject) {
			if (mh.getMaMH().equals(id)) {
				for (SinhVien sv : listStudent) {
					for (MonHoc m : sv.getSvTKB()) {
						if (m.getMaMH().equals(id)) {
							listStudentsOfSubject.add(sv);
						}
					}
				}
			}
		}
		return listStudentsOfSubject;
	}

	/* method find Subject Of Teacher by ID Teacher */
	public static List<MonHoc> findSubjectbyIDTeacher(String id) {
		listSubjectOfTeacher = new ArrayList<>();
		for (GiangVien gv : listTeacher) {
			if (gv.getId().equals(id)) {
				for (MonHoc mh : gv.getGvTKB()) {
					for (MonHoc m : listSubject) {
						if (mh.getMaMH().equals(m.getMaMH())) {
							listSubjectOfTeacher.add(m);
						}
					}
				}
			}
		}
		return (ArrayList<MonHoc>) listSubjectOfTeacher;
	}

	public static void bruteforce(List input) { // ham xoa bo du lieu trung nhau trong danh sach
		for (int i = 0; i < input.size(); i++) {
			for (int j = 0; j < input.size(); j++) {
				if (input.get(i).equals(input.get(j)) && i != j) {
					input.remove(j);
				}
			}
		}
	}

}
