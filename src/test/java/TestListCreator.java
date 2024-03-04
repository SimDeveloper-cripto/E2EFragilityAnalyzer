
import java.util.List;

public class TestListCreator {
    public static void createPhormerTestList(List<String> fileNames) {
        fileNames.add("src/test/java/JUnit/Phormer/LoginTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/CreateCategoryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/ModifyCategoryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/DeleteCategoryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/CreateStoryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/ModifyStoryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/DeleteStoryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/AddNewPhotoTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/DeletePhotoTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/Selectlast20Test.java");
        fileNames.add("src/test/java/JUnit/Phormer/PhotoGalleryTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/RSSTest.java");
        fileNames.add("src/test/java/JUnit/Phormer/LogoutTest.java");
    }

    public static void createJTracTestList(List<String> fileNames) {
        fileNames.add("src/test/java/JUnit/JTrac/SetItalianLanguageTest.java");
        fileNames.add("src/test/java/JUnit/JTrac/LoginAdminTest.java");
        fileNames.add("src/test/java/JUnit/JTrac/CreateUser1Test.java");
        fileNames.add("src/test/java/JUnit/JTrac/CreateUser2Test.java");
        fileNames.add("src/test/java/JUnit/JTrac/ModifyUser1Test.java");
        fileNames.add("src/test/java/JUnit/JTrac/DeleteUser2Test.java");
        fileNames.add("src/test/java/JUnit/JTrac/CreateSpaceTest.java");
        fileNames.add("src/test/java/JUnit/JTrac/SettingsTest.java");
        fileNames.add("src/test/java/JUnit/JTrac/LogoutAdminTest.java");
        fileNames.add("src/test/java/JUnit/JTrac/LoginUser1Test.java");
        fileNames.add("src/test/java/JUnit/JTrac/BlackboardTest.java");
    }

    public static void createPasswordManagerTestList(List<String> fileNames) {
        fileNames.add("src/test/java/JUnit/PasswordManager/RegisterTest.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/LoginTest.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/AddEntry1Test.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/AddEntry2Test.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/ModifyEntry1Test.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/ModifyEntry2Test.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/ExportCSVTest.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/DeleteEntry1Test.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/DeleteEntry2Test.java");
        fileNames.add("src/test/java/JUnit/PasswordManager/ImportCSVTest.java");
    }

    public static void createAddressBookTestList(List<String> fileNames, String version) {
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookAddAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookSearchAddressBookNameTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookSearchAddressBookEmailTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookAddGroupTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookAssignToGroupTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookSearchByGroupTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookCheckBirthdayInfoTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookCheckAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookPrintAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookEditAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookEditGroupTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookRemoveFromGroupTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookRemoveGroupTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookRemoveAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookAddMultipleAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookSearchMultipleAddressBookNameTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookAddMultipleGroupsTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookAssignToMultipleGroupsTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookSearchByMultipleGroupsTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookCheckMultipleBirthdaysInfoTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookCheckMultipleAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookPrintMultipleAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookEditMultipleAddressBookTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookEditMultipleGroupsTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookRemoveFromMultipleGroupsTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookRemoveMultipleGroupsTest.java");
        fileNames.add("src/test/java/JUnit/AddressBook" + version + "/AddressBookRemoveMultipleAddressBookTest.java");
    }
}